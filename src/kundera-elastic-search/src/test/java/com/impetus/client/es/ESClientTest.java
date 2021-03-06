/*******************************************************************************
 * * Copyright 2013 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.client.es;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.impetus.client.es.PersonES.Day;
import com.impetus.kundera.Constants;
import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.configure.ClientFactoryConfiguraton;
import com.impetus.kundera.metadata.KunderaMetadataManager;
import com.impetus.kundera.metadata.MetadataBuilder;
import com.impetus.kundera.metadata.model.ApplicationMetadata;
import com.impetus.kundera.metadata.model.ClientMetadata;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.metadata.model.KunderaMetadata;
import com.impetus.kundera.metadata.model.MetamodelImpl;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import com.impetus.kundera.metadata.processor.IndexProcessor;
import com.impetus.kundera.metadata.processor.TableProcessor;

public class ESClientTest
{
    private final static String persistenceUnit = "es-pu";

    private Node node = null;

    @Before
    public void setup() throws Exception
    {
        if (!checkIfServerRunning())
        {
            ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
            builder.put("path.data", "target/data");
            node = new NodeBuilder().settings(builder).node();
        }
        getEntityManagerFactory();
    }

    @Test
    public void test() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
    {
        ESClientFactory esFactory = new ESClientFactory();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(PersistenceProperties.KUNDERA_NODES, "localhost");
        props.put(PersistenceProperties.KUNDERA_PORT, "9300");

        Field f = esFactory.getClass().getSuperclass().getDeclaredField("persistenceUnit");

        if (!f.isAccessible())
        {
            f.setAccessible(true);
        }
        f.set(esFactory, persistenceUnit);
        esFactory.load(persistenceUnit, props);

        ESClient client = (ESClient) esFactory.getClientInstance();

        EntityMetadata metadata = KunderaMetadataManager.getEntityMetadata(PersonES.class);

        PersonES entity = new PersonES();
        entity.setAge(21);
        entity.setDay(Day.FRIDAY);
        entity.setPersonId("1");
        entity.setPersonName("vivek");
        client.onPersist(metadata, entity, "1", null);

        PersonES result = (PersonES) client.find(PersonES.class, "1");
        Assert.assertNotNull(result);

        PersonES invalidResult = (PersonES) client.find(PersonES.class, "2_p");
        Assert.assertNull(invalidResult);

        client.delete(result, "1");
        result = (PersonES) client.find(PersonES.class, "1");
        Assert.assertNull(result);
    }

    private void getEntityManagerFactory()
    {
        ClientMetadata clientMetadata = new ClientMetadata();
        Map<String, Object> props = new HashMap<String, Object>();

        props.put(Constants.PERSISTENCE_UNIT_NAME, persistenceUnit);
        props.put(PersistenceProperties.KUNDERA_CLIENT_FACTORY, ESClientFactory.class.getName());
        props.put(PersistenceProperties.KUNDERA_NODES, "localhost");
        props.put(PersistenceProperties.KUNDERA_PORT, "9300");
        props.put(PersistenceProperties.KUNDERA_KEYSPACE, "esSchema");
        clientMetadata.setLuceneIndexDir(null);

        KunderaMetadata.INSTANCE.setApplicationMetadata(null);
        ApplicationMetadata appMetadata = KunderaMetadata.INSTANCE.getApplicationMetadata();
        PersistenceUnitMetadata puMetadata = new PersistenceUnitMetadata();
        puMetadata.setPersistenceUnitName(persistenceUnit);
        Properties p = new Properties();
        p.putAll(props);
        puMetadata.setProperties(p);
        Map<String, PersistenceUnitMetadata> metadata = new HashMap<String, PersistenceUnitMetadata>();
        metadata.put(persistenceUnit, puMetadata);
        appMetadata.addPersistenceUnitMetadata(metadata);

        Map<String, List<String>> clazzToPu = new HashMap<String, List<String>>();

        List<String> pus = new ArrayList<String>();
        pus.add(persistenceUnit);

        MetadataBuilder metadataBuilder = new MetadataBuilder(persistenceUnit, ESClient.class.getSimpleName(), null);

        EntityMetadata entityMetadata = new EntityMetadata(PersonES.class);

        //
        TableProcessor processor = new TableProcessor(null);
        processor.process(PersonES.class, entityMetadata);

        IndexProcessor indexProcessor = new IndexProcessor();
        indexProcessor.process(PersonES.class, entityMetadata);
        //
        entityMetadata.setPersistenceUnit(persistenceUnit);

        //
        MetamodelImpl metaModel = new MetamodelImpl();
        metaModel.addEntityMetadata(PersonES.class, metadataBuilder.buildEntityMetadata(PersonES.class));

        appMetadata.getMetamodelMap().put(persistenceUnit, metaModel);
        //
        metaModel.assignManagedTypes(appMetadata.getMetaModelBuilder(persistenceUnit).getManagedTypes());
        metaModel.assignEmbeddables(appMetadata.getMetaModelBuilder(persistenceUnit).getEmbeddables());
        metaModel.assignMappedSuperClass(appMetadata.getMetaModelBuilder(persistenceUnit).getMappedSuperClassTypes());

        // cla

        String[] persistenceUnits = new String[] { persistenceUnit };
        clazzToPu.put(PersonES.class.getName(), Arrays.asList(persistenceUnits));

        appMetadata.setClazzToPuMap(clazzToPu);

        new ClientFactoryConfiguraton(null, persistenceUnits).configure();
    }

    @After
    public void tearDown() throws Exception
    {
        if (checkIfServerRunning())
        {
            node.close();
        }
        KunderaMetadata.INSTANCE.setApplicationMetadata(null);
    }

    /**
     * Check if server running.
     * 
     * @return true, if successful
     */
    private static boolean checkIfServerRunning()
    {
        try
        {
            Socket socket = new Socket("127.0.0.1", 9300);
            return socket.getInetAddress() != null;
        }
        catch (UnknownHostException e)
        {
            return false;
        }
        catch (IOException e)
        {
            return false;
        }
    }
}
