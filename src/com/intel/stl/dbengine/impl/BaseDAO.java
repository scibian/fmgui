/**
 * Copyright (c) 2015, Intel Corporation
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Intel Corporation nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intel.stl.dbengine.impl;

import javax.persistence.EntityManager;

import com.intel.stl.dbengine.DatabaseContext;

public class BaseDAO {
    protected final EntityManager em;

    protected final DatabaseContext databaseCtx;

    public BaseDAO(EntityManager entityManager) {
        this(entityManager, null);
    }

    public BaseDAO(EntityManager entityManager, DatabaseContext databaseCtx) {
        this.em = entityManager;
        this.databaseCtx = databaseCtx;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public DatabaseContext getDatabaseContext() {
        return databaseCtx;
    }

    public void startTransaction() {
        em.getTransaction().begin();
    }

    public void commitTransaction() {
        em.getTransaction().commit();
    }

    public void rollbackTransaction() {
        em.getTransaction().rollback();
    }

    public void flush() {
        em.flush();
    }

    public void clear() {
        em.clear();
    }
}
