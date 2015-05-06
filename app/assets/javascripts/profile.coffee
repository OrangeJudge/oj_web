problemLink = (problem) ->
  $a = $("<a>")
  $a.attr("href", "/problem/" + problem.slug)
  $a.html problem.title
  $a

solvedProblemTemplate = $("#solved-problem-list-item-template").html()
displaySolvedProblems = (problemList) ->
  $ul = $("#solved-problem-list").find("ul")
  $ul.html ""
  for i in [0..4]
    if i < problemList.length
      problem = problemList[i]
      $li = $(solvedProblemTemplate)
      $li.find(".problem").html problemLink(problem)
      $ul.append($li)
    else
      $ul.append(solvedProblemTemplate)


getSolvedProblems = ->
  $.ajax
    url: "/asyn/user/" + window.profileUserName + "/solved-problems"
    success: (ret) ->
      if ret.status == 0
        displaySolvedProblems(ret.data)

$(document).ready ->
  $(".country").html window.code_to_country[$(".country").html()]
  getSolvedProblems()
