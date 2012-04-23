package org.zanata.action;

import java.util.Collection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.security.Restrict;

@Name("reindexAction")
@Scope(ScopeType.APPLICATION)
@Startup
@Restrict("#{s:hasRole('admin')}")
public class ReindexActionBean
{

   @In
   ReindexAsyncBean reindexAsync;

   public Collection<ReindexClassOptions> getClasses()
   {
      return reindexAsync.getReindexOptions();
   }

   public boolean isReindexing()
   {
      return reindexAsync.isReindexing();
   }

   public boolean isReindexError()
   {
      return reindexAsync.hasError();
   }

   public int getReindexCount()
   {
      return reindexAsync.getObjectCount();
   }

   public int getReindexProgress()
   {
      return reindexAsync.getObjectProgress();
   }

   public void reindexDatabase()
   {
      if (!reindexAsync.isReindexing())
      {
         reindexAsync.prepareReindex();
         reindexAsync.startReindex();
      }
   }

}