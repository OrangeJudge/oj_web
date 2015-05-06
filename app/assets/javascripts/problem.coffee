codeEditor = null

codeMode = {
  10: "ace/mode/c_cpp"
  20: "ace/mode/c_cpp"
  30: "ace/mode/java"
}

c_template = """#include <stdio.h>

  int main() {
      printf("Hello World");
      return 0;
  }
  """

cpp_template = """#include <iostream>

  using namespace std;

  int main() {
      cout << "Hello World" << endl;
      return 0;
  }
  """

java_template = """class Main {
      public static void main(String[] args) {
          System.out.println("Hello World!");
      }
  }
  """

codeTemplate = {
  10: c_template
  20: cpp_template
  30: java_template
}

setupCodeEditor = ->
  codeEditor = ace.edit("code")
  codeEditor.setTheme("ace/theme/chrome")
  codeEditor.getSession().setMode("ace/mode/c_cpp")
  codeEditor.$blockScrolling = Infinity;
  onLanguageChange()

onLanguageChange = ->
  languageCode = $("#language").val()
  codeEditor.getSession().setValue(codeTemplate[languageCode])
  codeEditor.getSession().setMode(codeMode[languageCode])

displayMessage = (msg) ->
  $("#submit-form").find(".message").html(msg)

submitFormHandler = ->
  displayMessage "Submitting"
  language_code = $("#language").val()
  code = codeEditor.getSession().getValue()
  slug = $("#slug").val()
  submit_package =
    language: language_code
    code: code
  $.ajax
    url: "/asyn/problem/" + slug + "/submit"
    type: "POST"
    data: JSON.stringify(submit_package)
    contentType: 'application/json'
    dataType: "json"
    success: (ret) ->
      if ret.status
        displayMessage ret.message
        toastr.error ret.message
      else
        displayMessage "Succeeded."
        toastr.success "Your code is successfully submitted."
        getSolutionList()
  return false

mySolutionItemTemplate = $("#my-submission-item-template").html()
displayMySolution = (solutionList) ->
  $ul = $("#my-submission-list").find('ul')
  $ul.html ""
  if !solutionList
    solutionList = []
  for i in [0...5]
    if i >= solutionList.length
      $ul.append $(mySolutionItemTemplate)
      continue
    solution = solutionList[i]
    if solution["result"] >= 100 and solution["judgeResponse"] != null
      result = solution["judgeResponse"].split("\n")[0]
    else
      result = "Submitted"
    time = moment(solution["submitTime"]).fromNow()
    time = $("<a>").attr("href", "/solution/" + solution["id"]).html time
    $item = $(mySolutionItemTemplate)
    $item.find(".result").html result
    $item.find(".time").html(solution["timeUsed"])
    $item.find(".submitTime").html(time)
    $ul.append($item)

allSolutionItemTemplate = $("#all-submission-item-template").html()
displayAllSolution = (solutionList) ->
  $ul = $("#all-submission-list").find("ul")
  $ul.html ""
  if !solutionList
    solutionList = []
  for i in [0...5]
    if i >= solutionList.length
      $ul.append $(allSolutionItemTemplate)
      continue
    solution = solutionList[i]
    if solution["result"] >= 100 and solution["judgeResponse"] != null
      result = solution["judgeResponse"].split("\n")[0]
    else
      result = "Submitted"
    time = moment(solution["submitTime"]).fromNow()
    time = $("<a>").attr("href", "/solution/" + solution["id"]).html time
    $item = $(allSolutionItemTemplate)
    $item.find(".result").html result
    $item.find(".user").html(solution["user"]["name"])
    $item.find(".submitTime").html(time)
    $ul.append($item)

getSolutionList = ->
  slug = $("#slug").val()
  $.ajax
    url: "/asyn/solution/recent?problem=" + slug + "&user=" + window.username + "&pageSize=5"
    success: (ret) ->
      solutionList = ret['data']
      displayMySolution(solutionList)
  $.ajax
    url: "/asyn/solution/recent?problem=" + slug + "&result=200&pageSize=5"
    success: (ret) ->
      solutionList = ret['data']
      displayAllSolution(solutionList)
      setTimeout(getSolutionList, 3000)

onPageLoad = ->
  setupCodeEditor()
  $("#submit-form").submit submitFormHandler
  $("#language").on "change", onLanguageChange
  getSolutionList()

$(document).ready onPageLoad()