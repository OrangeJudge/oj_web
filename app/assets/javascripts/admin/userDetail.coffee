window.deleteUser = ->
  $.ajax
    url: "/admin/user/" + window.editingUserId
    type: "delete"
    success: (ret) ->
      if ret.status
        alert ret.message
      else
        alert "User deleted."
        window.location = "/admin/user"