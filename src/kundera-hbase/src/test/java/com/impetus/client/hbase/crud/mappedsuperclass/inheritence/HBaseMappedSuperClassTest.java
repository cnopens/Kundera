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
package com.impetus.client.hbase.crud.mappedsuperclass.inheritence;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.MappedSuperclass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.impetus.client.hbase.junits.HBaseCli;
import com.impetus.kundera.client.crud.mappedsuperclass.MappedSuperClassBase;

/**
 * @author vivek.mishra
 * junit for {@link MappedSuperclass}, {@link AttributeOverride}, {@link AttributeOverrides}.
 */
public class HBaseMappedSuperClassTest extends MappedSuperClassBase
{
    
    private HBaseCli cli;

    @Before
    public void setUp() throws Exception
    {
        _PU = "hbaseTest";
        cli = new HBaseCli();
        cli.startCluster();
        setUpInternal();
    }

    @Test
    public void test()
    {
        assertInternal();
    }


    /**
     * Tear down.
     * 
     * @throws Exception
     *             the exception
     */
    @After
    public void tearDown() throws Exception
    {
        if (cli != null )
        {
            cli.dropTable("KunderaExamples");
            cli.stopCluster("KunderaExamples");
        }
        tearDownInternal();
    }

}
