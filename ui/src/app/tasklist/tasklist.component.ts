import { Component, OnInit } from '@angular/core';
import { AppService } from '../app.service';
import { Task } from '../task';
@Component({
  selector: 'app-tasklist',
  templateUrl: './tasklist.component.html',
  styleUrls: ['./tasklist.component.scss']
})
export class TasklistComponent implements OnInit {
  tasks: Task[] ;
  restBaseURL : string =  "http://rhpam73-kieserver-rhpam-73-auth.52.179.103.114.nip.io:80/services/rest/server";
  constructor(private appService: AppService) {
    // this.appService.getWelcomeMessage().subscribe((data: any) => {
    //  });
    }

  ngOnInit() {
      this.refreshTasks();
  }
  public refreshTasks(): void {
    this.appService.sendDataURL("/api/listTasks").subscribe((data: any) => {
      this.tasks = data.content;
      console.log("Refreshed tasks - got tasks: "+data.content.length);
    });
  }

  public showProcess(processName,processInstanceId){
      window.open(this.restBaseURL+"/containers/"+processName+"/images/processes/instances/"+processInstanceId ,"_blank","height=1024,width=768" );
  }

  public invokeTask(taskId,containerId,taskStatus){
      this.appService.sendDataURL("/api/initTask/"+taskId+"/"+containerId).subscribe((data: any) => {
          console.log("returned from init task: "+data);
          window.location.href = "/runTask/"+taskId+"/"+containerId;
    });

    }
}
