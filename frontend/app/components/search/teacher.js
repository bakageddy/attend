import Component from "@ember/component";
import { service } from '@ember/service';
import { action } from '@ember/object';
import { tracked } from "@glimmer/tracking";

export default class TeacherComponent extends Component {
  @service('result_handler') service;

  @tracked teacherid    = undefined;
  @tracked teachername  = undefined;

  @action
  async search_teacher_id() {
    if (this.teacherid === undefined || this.teacherid <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'id': this.teacherid,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/teacher/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'teacher',
      'data' : [data]
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_teacher_name() {
    if (this.teachername.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': this.teachername,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/teacher/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'teacher',
      'data' : data
    };
    this.service.results = results;
    console.log(data);
  }
}
