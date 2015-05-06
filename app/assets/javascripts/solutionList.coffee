user = null
problem = null
resultCode = null

updateJudgeStatus = ->
  displayJudgeStatus = (solutionList) ->
    solutionItemTemplate = $("#solution-item-template").html()
    $("#solution-list").html ""
    for solution in solutionList
      result = getSolutionResultBlock(solution["result"], solution["judgeResponse"])
      time = moment(solution["submitTime"]).fromNow()
      time = $("<a>").attr("href", "/solution/" + solution["id"]).html time
      $li = $("<li>")
      $li.html solutionItemTemplate
      $li.find(".id").html(solution["id"])
      $li.find(".problem a")
          .attr("href", "/problem/" + solution["problem"]["slug"])
          .html(solution["problem"]["slug"] + ". " + solution["problem"]["title"])
      $li.find(".user").html(solution["user"]["displayName"])
      $li.find(".result").html(result)
      $li.find(".language").html(solution["language"])
      $li.find(".memory").html(solution["memoryUsed"])
      $li.find(".time").html(solution["timeUsed"])
      $li.find(".submitTime").html(time)
      $("#solution-list").append($li)
  query = ""
  if user
    if window.username and user == window.username
      $("#solution-list-li").removeClass "active"
      $("#my-submission-li").addClass "active"
    query += "user=" + user
  if problem
    if query.length > 0
      query += "&"
    query += "problem=" + problem
  if resultCode
    if query.length > 0
      query += "&"
    query  += "result=" + resultCode
  if query.length > 0
    query = "?" + query
  $.ajax
    url: "/asyn/solution/recent" + query
    success: (ret) ->
      if ! ret["status"]
        displayJudgeStatus(ret["data"])
      else
        alert ret["message"]
      setTimeout(updateJudgeStatus, 10000)

onPageLoad = ->
  user = window.getUrlParameter("user")
  problem = window.getUrlParameter("problem")
  resultCode = window.getUrlParameter("result")
  if user
    $("#user-name").html user
  if problem
    $("#problem-name").html problem
  updateJudgeStatus()

$(document).ready onPageLoad