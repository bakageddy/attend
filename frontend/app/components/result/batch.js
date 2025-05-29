import Component from "@glimmer/component";
import { action } from "@ember/object"

export default class BatchComponent extends Component {
  @action
  handleClick(ev) {
    let batchid = ev.currentTarget.querySelector(".batch__result__id").innerText;
    let teacherid = ev.currentTarget.querySelector(".batch__result__teacherid").innerText;
    batchid = Number.parseInt(batchid);
    teacherid = Number.parseInt(teacherid);

    let batch_attendance_batchid_input = document.getElementById("batch__attendance__form__batchid");
    let batch_attendance_teacherid_input = document.getElementById("batch__attendance__form__teacherid");
    let batch_crud_batchid_input = document.getElementById("batch__crud__batchid__input");
    let batch_crud_teacherid_input = document.getElementById("batch__crud__teacherid__input");

    if (batch_attendance_batchid_input != null && batch_attendance_teacherid_input != null) {
      batch_attendance_batchid_input.value = batchid;
      batch_attendance_teacherid_input.value = teacherid;
    }

    if (batch_crud_batchid_input !== null && batch_crud_teacherid_input !== null) {
      batch_crud_batchid_input.value = batchid;
      batch_crud_teacherid_input.value = teacherid;
    }

    return;
  }

  @action
  handleDragStart(ev) {
    ev.preventDefault();
  }
}
