$(document).ready ->
  $createTimeDivs = $(".user-list ul").find(".createTime")
  for div in $createTimeDivs
    $(div).html moment($(div)).fromNow()