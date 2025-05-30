import Component from "@ember/component";
import { service } from '@ember/service';
import { action } from '@ember/object';
import { tracked } from "@glimmer/tracking";

export default class StudentComponent extends Component {
  @service('result_handler') service;
  @service('error_service') error;
  @service router;

  @tracked studentid    = undefined;
  @tracked studentname  = undefined;

  @action
  async search_student_id(ev) {
    this.studentid = ev.target.value;
    if (this.studentid == undefined || this.studentid <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'rollno': this.studentid,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/student/search?" + params,
    );

    if (response.status != 200) {
      // This is a bad request with malformed parameters
      if (response.status == 400) {
        this.error.set("Something went wrong! Try checking your search parameters (expect number)");
        this.router.transitionTo("error");
      } else if (response.status == 204) {
        this.service.results = [];
      } else if (response.status == 500) {
        this.error.set("Something went wrong with the server! We are working on it!");
        this.router.transitionTo("error");
      }
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'student',
      'data' : [data]
    };

    this.service.results = results;
    console.log(this.service.results);
  }

  @action
  async search_student_name(ev) {
    this.studentname = ev.target.value;
    if (this.studentname.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': this.studentname,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/student/search?" + params,
    );

    if (response.status !== 200) {
      if (response.status === 400) {
        this.error.set("Something went wrong! Try checking your search parameters (expect text)");
        this.router.transitionTo("error");
      } else if (response.status == 204) {
        this.service.results = [];
      } else if (response.status == 500) {
        this.error.set("Something went wrong with the server! We are working on it!");
        this.router.transitionTo("error");
      }
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'student',
      'data' : data
    };
    this.service.results = results;
    console.log(results);
  }
}
