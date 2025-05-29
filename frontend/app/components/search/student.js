import Component from "@ember/component";
import { service } from '@ember/service';
import { action } from '@ember/object';
import { tracked } from "@glimmer/tracking";

export default class StudentComponent extends Component {
  @service('result_handler') service;

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

    if (response.status != 200) {
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
