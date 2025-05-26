import Component from "@ember/component";

export default class SearchComponent extends Component {
  async search_student_id(_) {
    let value = document.getElementById("search__student__id").value;
    if (value <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'rollno': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/student/search?" + params,
      {headers: {
        "Access-Control-Allow-Origin": "http://localhost:8080"
      }}
    );

    let json = await response.json();
    console.log(json);
  }

  async search_student_name() {
    return;
  }
}
