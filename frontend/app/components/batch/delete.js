import Component from "@glimmer/component";
import { tracked } from "@glimmer/tracking";
import { action } from "@ember/object";

export default class DeleteComponent extends Component {
  @tracked teacherid = undefined;
  @tracked batchid = undefined;

  @action
  async handleSubmit(_) {
    if (this.teacherid === undefined && this.batchid === undefined) {
      // TODO: Render err dialog
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

    if (resp.status !== 200) {
      // TODO: Render err dialog
      return;
    }
    console.log("Deletion successful");
  }
}
