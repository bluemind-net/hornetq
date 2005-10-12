/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.messaging.core.local;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.transaction.TransactionManager;

import org.jboss.jms.server.endpoint.ServerConsumerDelegate;
import org.jboss.logging.Logger;
import org.jboss.messaging.core.MessageStore;
import org.jboss.messaging.core.PersistenceManager;
import org.jboss.messaging.core.TransactionalChannelSupport;
import org.jboss.util.id.GUID;

public class Subscription extends TransactionalChannelSupport
{
   // Constants -----------------------------------------------------
   
   private static final Logger log = Logger.getLogger(Subscription.class);

   // Static --------------------------------------------------------
   
   // Attributes ----------------------------------------------------
   
   protected Topic topic;
   
   protected String selector;
   
   // Constructors --------------------------------------------------

   public Subscription(Topic topic, String selector, MessageStore ms)
   {
      this(topic, selector, ms, null);
   }
   
   protected Subscription(Topic topic, String selector, MessageStore ms, PersistenceManager pm)
   {
      super("sub" + new GUID().toString(), ms, pm);
      this.topic = topic;
      this.selector = selector;
      router = new PointToPointRouter();
   }
   

   // Channel implementation ----------------------------------------

   public boolean isStoringUndeliverableMessages()
   {
      return false;
   }

   // Public --------------------------------------------------------
   
   public void subscribe()
   {
      topic.add(this);
   }
   
   public void unsubscribe() throws JMSException
   {
      topic.remove(this);      
   }
   
   public void closeConsumer(PersistenceManager pm) throws JMSException
   {
      unsubscribe();
      try
      {
         pm.removeAllMessageData(this.channelID);
      }
      catch (Exception e)
      {
         final String msg = "Failed to remove message data for subscription";
         log.error(msg, e);
         throw new IllegalStateException(msg);
      }
   }
   
   public Topic getTopic()
   {
      return topic;
   }
   
   public String getSelector()
   {
      return selector;
   }

   // Package protected ---------------------------------------------
   
   // Protected -----------------------------------------------------
   
   // Private -------------------------------------------------------
   
   // Inner classes -------------------------------------------------   
}
