package org.zanata.webtrans.client.events;


import org.zanata.rest.dto.stats.CommonContainerTranslationStatistics;
import org.zanata.webtrans.shared.model.DocumentId;

import com.google.gwt.event.shared.GwtEvent;

public class DocumentStatsUpdatedEvent extends GwtEvent<DocumentStatsUpdatedEventHandler>
{

   /**
    * Handler type.
    */
   private static Type<DocumentStatsUpdatedEventHandler> TYPE;

   private DocumentId docId;
   private CommonContainerTranslationStatistics newStats;

   /**
    * Gets the type associated with this event.
    * 
    * @return returns the handler type
    */
   public static Type<DocumentStatsUpdatedEventHandler> getType()
   {
      return TYPE != null ? TYPE : (TYPE = new Type<DocumentStatsUpdatedEventHandler>());
   }

   public DocumentStatsUpdatedEvent(DocumentId docId, CommonContainerTranslationStatistics newStats)
   {
      this.docId = docId;
      this.newStats = newStats;
   }

   public DocumentId getDocId()
   {
      return docId;
   }

   public CommonContainerTranslationStatistics getNewStats()
   {
      return newStats;
   }

   @Override
   protected void dispatch(DocumentStatsUpdatedEventHandler handler)
   {
      handler.onDocumentStatsUpdated(this);
   }

   @Override
   public GwtEvent.Type<DocumentStatsUpdatedEventHandler> getAssociatedType()
   {
      return getType();
   }
}
