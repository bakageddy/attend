import Component from "@glimmer/component";
import { tracked } from "@glimmer/tracking";
import { action } from "@ember/object";
import { service } from "@ember/service";

export default class DeleteComponent extends Component {
  @service("error_service") error;
  @service router;

  @tracked teacherid = undefined;
  @tracked batchid = undefined;

  @action
  async handleSubmit(_) {
    if (this.teacherid === undefined && this.batchid === undefined) {
      this.error.set("Do not submit with empty fields");
      this.router.transitionTo("error");
      return;
    }

    let params = new URLSearchParams({
      'teacherid': this.teacherid,
      'batchid': this.batchid
    }).toString();

    let resp = await fetch(
      "http://localhost:8080/app/api/batch?" + params,
      { method: "DELETE" }
    );

    if (resp.statusCode !== 200) {
      if (resp.statusCode !== 400) {
        this.error.set("Something went wrong! Double Check the input form");
      } else if (resp.statusCode !== 500) {
        this.error.set("Something went wrong with the server! We are fixing it");
      }
      this.router.transitionTo("error");
      return;
    }
    console.log("Deletion successful");
  }
}
