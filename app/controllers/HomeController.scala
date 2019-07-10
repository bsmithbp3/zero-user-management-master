package controllers

import javax.inject._
import models.{ Task}
import play.api.libs.ws._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext // for rest api context - ec
@Singleton
class HomeController @Inject()(ws: WSClient, cc: MessagesControllerComponents, ec: ExecutionContext) extends MessagesAbstractController(cc) {
  //  class HomeController @Inject()(ws: WSClient,cc: ControllerComponents) extends AbstractController(cc) {
  val baseURL = "http://rhpam73-kieserver-rhpam-73-auth.52.179.103.114.nip.io:80/services/rest/server"
  val user = "adminUser"
  val password = "TELrIt2!"
  def appSummary = Action {
    Ok(Json.obj("content" -> "the NCL Process Task List"))
  }

  def postTest = Action {
    Ok(Json.obj("content" -> "<p>zzzz</p>aBill Smith Post Request Test => Data Sending Success"))
  }

  def completeTask(tid: String, containerId: String) = Action.async {

    implicit val ec = ExecutionContext.global

    completeTaskWS(tid, containerId);
    val listRequest: WSRequest = ws.url(baseURL + "/queries/tasks/instances/pot-owners?pageSize=25")
      .withAuth(user, password, WSAuthScheme.BASIC)
      .addHttpHeaders("Content-Type" -> "application/json")

    listRequest.get().map { response =>
      response.json
      val summary = response.json \ "task-summary"
      println("*******************************************************************");
      println("resp : " + response.json)
      println("class of summary is : " + summary.getClass)
      println("class of summary.get is : " + summary.get.getClass)
      val tasks = summary.get.as[List[Task]]
      val emptyArray = Json.arr()
      var filledArray = emptyArray
      for (t <- tasks) {
        filledArray = filledArray :+ Json.obj("taskId" -> t.taskId,
          "processInstanceId" -> t.processInstanceId,
          "processName" -> t.processName,
          "taskName" -> t.taskName,
          "taskDescription" -> t.taskDescription,
          "taskContainerId" -> t.taskContainerId,
          "taskStatus" -> t.taskStatus,
          "taskOwner" -> t.taskOwner,
          "creationDate" -> t.creationDate)
      }
      //Ok(Json.obj("content" -> summary.get))

      Ok(Json.obj("content" -> filledArray))
    }


  }
  def listTasks = Action.async {

    implicit val ec = ExecutionContext.global
    val listRequest: WSRequest = ws.url(baseURL + "/queries/tasks/instances/pot-owners?pageSize=25")
      .withAuth(user, password, WSAuthScheme.BASIC)
      .addHttpHeaders("Content-Type" -> "application/json")

    listRequest.get().map { response =>
      response.json
      val summary = response.json \ "task-summary"
      println("*******************************************************************");
      println("resp : " + response.json)
      println("class of summary is : " + summary.getClass)
      println("class of summary.get is : " + summary.get.getClass)
      val tasks = summary.get.as[List[Task]]
      val emptyArray = Json.arr()
      var filledArray = emptyArray
      for (t <- tasks) {
        filledArray = filledArray :+ Json.obj("taskId" -> t.taskId,
          "processInstanceId" -> t.processInstanceId,
          "processName" -> t.processName,
          "taskName" -> t.taskName,
          "taskDescription" -> t.taskDescription,
          "taskContainerId" -> t.taskContainerId,
          "taskStatus" -> t.taskStatus,
          "taskOwner" -> t.taskOwner,
          "creationDate" -> t.creationDate)
      }
      //Ok(Json.obj("content" -> summary.get))

      Ok(Json.obj("content" -> filledArray))
    }


  }

  def listTasksOLD = Action {
    Ok(Json.obj("content" -> Json.arr(
      Json.obj(

        "taskName" -> "task1234",
        "taskDescription" -> "This is the description for task1234"
      ),
      Json.obj(
        "taskName" -> "task5678",
        "taskDescription" -> "Thisis the desc for 5678"
      )
    )))
  }
  def completeTaskWS(tid: String, containerId: String) = {
    println("complete task .........." + baseURL + "/containers/" + containerId + "/tasks/" + tid + "/states/completed");
    val response = ws.url(baseURL + "/containers/" + containerId + "/tasks/" + tid + "/states/completed")
      .withAuth(user, password, WSAuthScheme.BASIC)
      .addHttpHeaders("Content-Type" -> "application/json").put("");

    Await.result(response, 10 seconds)
    println("complete is done - " + response.toString());
  }
  def initTask(tid: String, containerId: String) = Action.async { implicit request: MessagesRequest[AnyContent] =>
    implicit val ec = ExecutionContext.global

    val taskQuery: WSRequest = ws.url(baseURL + "/containers/" + containerId + "/tasks/" + tid + "?withInputData=true&withOutputData=true")

    val complexTaskRequest: WSRequest =
      taskQuery.withAuth(user, password, WSAuthScheme.BASIC)
        .addHttpHeaders("Content-Type" -> "application/json")

    println("calling init request")
    complexTaskRequest.get().map { response =>
      println("show request response: " + response.json.toString())
      val taskName = (response.json \ "task-name").get.toString().stripPrefix("\"").stripSuffix("\"")
      val status = (response.json \ "task-status").get.toString().stripPrefix("\"").stripSuffix("\"")
      println("task status: " + status);
      if (status == "Ready") {
        claimTask(tid, containerId)
        startTask(tid, containerId);
      }
      else if (status == "Reserved") {
        startTask(tid, containerId)
      }

      ////////////////////////////////////////////////////////////////////////////
      // This long term should be done in a different way like via some  rest call
      // and should also consider the process container as well
      //////////////////////////////////////////////////////////////////////////////

      if (taskName == "Request Access") {

        val inputData = response.json \ "task-input-data"
        val userName = Option((response.json \ "task-input-data" \ "user")).get
        val userId = (response.json \ "task-input-data" \ "userId").get
        val userEmail = (response.json \ "task-input-data" \ "comments").get
        val requestername = (response.json \ "task-input-data" \ "comments").get
        val requesterId = (response.json \ "task-input-data" \ "requesterId").get
        val asset = (response.json \ "task-input-data" \ "comments").get
        println("requester: " + (response.json \ "task-input-data" \ "requesterId").get)
        //response.json
        println("response: " + response.json.toString)
        Ok(Json.obj("content" -> taskName))
      }
      else {
        println("taskName: " + taskName.toString().stripPrefix("\"").stripSuffix("\""))
        Ok(Json.obj("content" -> taskName))
      }


    } // end mapping response

  } // end method
  def claimTask(tid: String, containerId: String) = {
    println("claim task .........." + baseURL + "/containers/" + containerId + "/tasks/" + tid + "/states/claimed");
    val response = ws.url(baseURL + "/containers/" + containerId + "/tasks/" + tid + "/states/claimed")
      .withAuth(user, password, WSAuthScheme.BASIC)
      .addHttpHeaders("Content-Type" -> "application/json").put("");

    Await.result(response, 10 seconds)
    println("claim is done - " + response.toString());
  }

  def startTask(tid: String, containerId: String) = {
    println("start task .........." + baseURL + "/containers/" + containerId + "/tasks/" + tid + "/states/started");
    val response = ws.url(baseURL + "/containers/" + containerId + "/tasks/" + tid + "/states/started")
      .withAuth(user, password, WSAuthScheme.BASIC)
      .addHttpHeaders("Content-Type" -> "application/json").put("");

    Await.result(response, 10 seconds)
    println("start is done - " + response.toString());
  }
}
