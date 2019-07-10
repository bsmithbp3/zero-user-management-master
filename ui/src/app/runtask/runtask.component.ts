import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../app.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-runtask',
  templateUrl: './runtask.component.html',
  styleUrls: ['./runtask.component.scss']
})
export class RunTaskComponent implements OnInit {
  isStarted : boolean = false;
  taskId: any;
  containerId: string;
  taskName : string="";
  paramsSub: any;
  restBaseURL : string =  "http://rhpam73-kieserver-rhpam-73-auth.52.179.103.114.nip.io:80/services/rest/server";
  constructor(private activatedRoute: ActivatedRoute,private appService: AppService,private _router: Router) { }


  ngOnInit() {

    this.paramsSub = this.activatedRoute.params.subscribe(params => {this.containerId = params['containerId'];
    this.taskId = params['taskId'];
    if(this.taskName == ""){
    // now go ahead and get the task in the inprogress state
    console.log("about to init:");
    console.log("/api/initTask/"+this.taskId+"/"+this.containerId);
    this.appService.sendDataURL("/api/initTask/"+this.taskId+"/"+this.containerId).subscribe((data: any) => {

                                          //this.tasks = data.content;
                                          console.log("Init Task task done got tasks"+data.content);
                                          this.taskName = data.content;
        });// end of sendData
        }
  });

  }
  ngOnDestroy() {
    this.paramsSub.unsubscribe();
  }

public async completeTask(tid: string, containerId:string): Promise<String> {
//this._router.navigate(['tasks']);

    console.log("just before complete");
    await this.appService.postDataURLSync("/api/completeTask/"+tid+"/"+containerId);
    console.log("complete task done");
    this._router.navigate(['tasks']);
    return ""

  }
public OLDcompleteTask(tid: string, containerId:string): void {
//this._router.navigate(['tasks']);
/*
    console.log("starting complete task in angular");
    this.appService.sendDataURL("/api/completeTask/"+tid+"/"+containerId).subscribe((data: any) => {

                                      //this.tasks = data.content;
                                      console.log("complete task done got tasks"+data.content.length);
                                      //this._router.navigate(['tasks']);
    });


    */
    console.log("just before complete");
    this.appService.postDataURLSync("/api/completeTask/"+tid+"/"+containerId);
    console.log("complete task done");
    this._router.navigate(['tasks']);


  }
}
