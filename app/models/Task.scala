package models


import play.api.libs.json._

/**
  * Presentation object used for displaying data in a template.
  *
  */
case class Task(taskId: String, processInstanceId: String, processName: String,
                taskName: String, taskDescription: String, taskContainerId: String, taskStatus: String, taskOwner: String, creationDate: String);

object Task {

  implicit val taskReads: Reads[Task] = new Reads[Task] {
    def reads(json: JsValue): JsResult[Task] = {

      for {

        taskId <- (json \ "task-id").get.validate[Int]
        taskName <- (json \ "task-name").get.validate[String]
        processInstanceId <- (json \ "task-proc-inst-id").get.validate[Int]
        processName <- (json \ "task-proc-def-id").get.validate[String]
        taskDescription <- (json \ "task-description").get.validate[String]
        creationDate <- (json \ "task-created-on" \ "java.util.Date").get.validate[Long]
        taskContainerId <- (json \ "task-container-id").get.validate[String]
        taskStatus <- (json \ "task-status").get.validate[String]
        taskOwner <- (json \ "task-actual-owner").get.validateOpt[String]

      } yield Task("" + taskId, "" + processInstanceId, processName, taskName, taskDescription, taskContainerId, taskStatus, taskOwner.getOrElse(""), (new java.util.Date(creationDate)).toString())
    }
  }


}



