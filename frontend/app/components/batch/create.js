import Component from "@glimmer/component";
import {action} from "@ember/object";
import { tracked } from "@glimmer/tracking";
import { service } from "@ember/service";

export default class CreateComponent extends Component {
  @service("error_service") error;
  @service router;

  @tracked teacherid = undefined;
  @tracked batchname = undefined;

  @action
  async handleSubmit(_) {
    if (this.teacherid === undefined && this.batchname === undefined) {
      // TODO: Render error dialog
      return;
    }

    let params = new URLSearchParams({
      'teacherid': this.teacherid,
      'name': this.batchname
    }).toString();

    let resp = await fetch("http://localhost:8080/app/api/batch?" + params, {
      method: "POST"
    });

    if (resp.statusCode !== 200) {
      if (resp.statusCode !== 400) {
        this.error.set("Something Went Wrong! Double check the form for more errors");
        router.transitionTo("error");
      } else if (resp.statusCode !== 500) {
        this.error.set("Something Went Wrong with the server! We are fixing it!");
        router.transitionTo("error");
      }
    }
  }
}
