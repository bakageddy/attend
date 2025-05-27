import Component from "@glimmer/component";
import { action } from "@ember/object"

export default class BatchResultComponent extends Component {
  @action
  handleClick(ev) {
    let batchid = ev.currentTarget.querySelector(".batch__result__id").innerText;
    batchid = Number.parseInt(batchid);

    let attendance_batchid_input = document.getElementById("attendance__form__batchid");
    let batch_attendance_batchid_input = document.getElementById("batch__attendance__form__batchid");

    attendance_batchid_input.value = teacherid;
    batch_attendance_batchid_input.value = teacherid;
    return;
  }
}
