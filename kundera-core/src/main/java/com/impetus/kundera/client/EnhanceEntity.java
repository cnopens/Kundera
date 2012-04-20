/*******************************************************************************
 * * Copyright 2011 Impetus Infotech.
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
package com.impetus.kundera.client;

import java.util.Collections;
import java.util.Map;

/**
 * The Class EnhanceEntity.
 * 
 * @author vivek.mishra
 */
public class EnhanceEntity
{

    /** The entity. */
    private Object entity;

    /** The entity id. */
    private String entityId;

    /** The relations. */
    private Map<String, Object> relations;

    public EnhanceEntity()
    {

    }

    /**
     * Instantiates a new enhance entity.
     * 
     * @param entity
     *            the entity
     * @param entityId
     *            the entity id
     * @param relations
     *            the relations
     */
    public EnhanceEntity(Object entity, String entityId, Map<String, Object> relations)
    {
        super();
        this.entity = entity;
        this.entityId = entityId;
        this.relations = relations;
    }

    /**
     * Gets the entity.
     * 
     * @return the entity
     */
    public Object getEntity()
    {
        return entity;
    }

    /**
     * Sets the entity.
     * 
     * @param entity
     *            the entity to set
     */
    public void setEntity(Object entity)
    {
        this.entity = entity;
    }

    /**
     * Gets the entity id.
     * 
     * @return the entityId
     */
    public String getEntityId()
    {
        return entityId;
    }

    /**
     * Sets the entity id.
     * 
     * @param entityId
     *            the entityId to set
     */
    public void setEntityId(String entityId)
    {
        this.entityId = entityId;
    }

    /**
     * Gets the relations.
     * 
     * @return the relations
     */
    public Map<String, Object> getRelations()
    {
        return relations != null ? Collections.unmodifiableMap(relations) : null;
    }

    /**
     * Sets the relations.
     * 
     * @param relations
     *            the relations to set
     */
    public void setRelations(Map<String, Object> relations)
    {
        this.relations = relations;
    }

}
