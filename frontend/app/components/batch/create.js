import Component from "@glimmer/component";
import {action} from "@ember/object";
import { tracked } from "@glimmer/tracking";

export default class CreateComponent extends Component {
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

    if (resp.status == 200) {
      // TODO: Show some response dialog
      console.log("Created batch!");
    }
  }
}
