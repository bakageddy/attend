import Component from "@glimmer/component";
import { service } from "@ember/service";
import { action } from "@ember/object";
import { tracked } from "@glimmer/tracking";

export default class BatchComponent extends Component {
  @service('result_handler') service;

  @tracked batchid    = undefined;
  @tracked teacherid  = undefined;
  @tracked batchname  = undefined;

  set batchid(val) {
    if (val > 0) {
      this.batchid = val;
    }
  }

  set teacherid(val) {
    if (val > 0) {
      this.teacherid = val;
    }
  }

  set batchname(val) {
    if (val.length > 0) {
      this.batchname = val;
    }
  }

  @action
  async search_batch_id(ev) {
    this.batchid = ev.target.value;
    if (this.batchid <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'batchid': this.batchid
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/batch/search?" + params
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'batch',
      'data': [data]
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_batch_teacherid(ev) {
    this.teacherid = ev.target.value;
    if (this.teacherid <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'teacherid': this.teacherid
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/batch/search?" + params
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'batch',
      'data': data
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_batch_name(ev) {
    this.batchname = ev.target.value;
    if (this.batchname.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': this.batchname
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/batch/search?" + params
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'batch',
      'data': data
    };
    this.service.results = results;
    console.log(results);
  }

}


