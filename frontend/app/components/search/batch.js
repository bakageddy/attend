import Component from "@glimmer/component";
import { service } from "@ember/service";
import { action } from "@ember/object";
import { tracked } from "@glimmer/tracking";

export default class BatchComponent extends Component {
  @service('result_handler') service;
  @service("error_service") error;
  @service router;

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
      if (response.status === 400) {
        this.error.set("Something went wrong! Try checking your search parameters (expect text)");
        console.log(this.error.from);
        this.router.transitionTo("error");
      } else if (response.status === 204) {
        this.service.clear();
      } else if (response.status === 500) {
        this.error.set("Something went wrong with the server! We are working on it!");
        this.router.transitionTo("error");
      }
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
      if (response.status === 400) {
        this.error.set("Something went wrong! Try checking your search parameters (expect text)");
        this.router.transitionTo("error");
      } else if (response.status === 204) {
        this.service.clear();
      } else if (response.status === 500) {
        this.error.set("Something went wrong with the server! We are working on it!");
        this.router.transitionTo("error");
      }
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

    if (response.status !== 200) {
      if (response.status === 400) {
        this.error.set("Something went wrong! Try checking your search parameters (expect text)");
        this.router.transitionTo("error");
      } else if (response.status === 204) {
        this.error.set("No such batch with the given Name/Pattern");
        this.service.clear();
      } else if (response.status === 500) {
        this.error.set("Something went wrong with the server! We are working on it!");
        this.router.transitionTo("error");
      }
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
