package org.springframework.data.rest.repository.spec

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.repository.RepositoryExporter
import org.springframework.data.rest.repository.annotation.HandleAfterDelete
import org.springframework.data.rest.repository.annotation.HandleAfterLinkSave
import org.springframework.data.rest.repository.annotation.HandleAfterSave
import org.springframework.data.rest.repository.annotation.HandleBeforeDelete
import org.springframework.data.rest.repository.annotation.HandleBeforeLinkSave
import org.springframework.data.rest.repository.annotation.HandleBeforeSave
import org.springframework.data.rest.repository.annotation.RepositoryEventHandler
import org.springframework.data.rest.repository.context.AfterDeleteEvent
import org.springframework.data.rest.repository.context.AfterLinkSaveEvent
import org.springframework.data.rest.repository.context.AfterSaveEvent
import org.springframework.data.rest.repository.context.AnnotatedHandlerRepositoryEventListener
import org.springframework.data.rest.repository.context.BeforeDeleteEvent
import org.springframework.data.rest.repository.context.BeforeLinkSaveEvent
import org.springframework.data.rest.repository.context.BeforeSaveEvent
import org.springframework.data.rest.repository.test.ApplicationConfig
import org.springframework.data.rest.repository.test.Person
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
@ContextConfiguration(classes = [ApplicationConfig, EventsApplicationConfig])
class ExtensionsSpec extends Specification {

  @Autowired
  ApplicationContext appCtx
  @Autowired
  PersonEventHandler handler
  @Autowired
  RepositoryExporter exporter

  def "responds to ApplicationEvents in annotated handlers"() {

    given:
    def p = new Person("John Doe")

    when:
    appCtx.publishEvent(new BeforeSaveEvent(p))
    appCtx.publishEvent(new AfterSaveEvent(p))
    appCtx.publishEvent(new BeforeLinkSaveEvent(p, new Object()))
    appCtx.publishEvent(new AfterLinkSaveEvent(p, new Object()))
    appCtx.publishEvent(new BeforeDeleteEvent(p))
    appCtx.publishEvent(new AfterDeleteEvent(p))

    then:
    handler.beforeSave
    handler.afterSave
    handler.beforeChildSave
    handler.afterChildSave
    handler.beforeDelete
    handler.afterDelete

  }

}

@Configuration
class EventsApplicationConfig {

  @Bean AnnotatedHandlerRepositoryEventListener repositoryEventListener() {
    new AnnotatedHandlerRepositoryEventListener("org.springframework.data.rest.repository.spec");
  }

  @Bean PersonEventHandler personEventHandler() {
    new PersonEventHandler()
  }

}

@RepositoryEventHandler(Person)
class PersonEventHandler {

  def beforeSave = false
  def afterSave = false
  def beforeChildSave = false
  def afterChildSave = false
  def beforeDelete = false
  def afterDelete = false

  @HandleBeforeSave void handleBeforeSave(Person p) {
    beforeSave = true
  }

  @HandleAfterSave void handleAfterSave(Person p) {
    afterSave = true
  }

  @HandleBeforeLinkSave void handleBeforeChildSave(Person p, Object child) {
    beforeChildSave = true
  }

  @HandleAfterLinkSave void handleAfterChildSave(Person p, Object child) {
    afterChildSave = true
  }

  @HandleBeforeDelete void handleBeforeDelete(Person p) {
    beforeDelete = true
  }

  @HandleAfterDelete void handleAfterDelete(Person p) {
    afterDelete = true
  }

}

