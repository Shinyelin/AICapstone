<?php
$con = mysqli_connect('', '', '', '');
$aId = $_POST["aId"];
$statement = mysqli_prepare($con, "SELECT aId from admin where aId = ?");
 mysqli_stmt_bind_param($statement, "s", $aId);
 mysqli_stmt_execute($statement);
 mysqli_stmt_store_result($statement);
 mysqli_stmt_bind_result($statement, $aId);
$response = array();
$response["success"] = true;
while(mysqli_stmt_fetch($statement)){
        $response["success"] = false;
        $response["aId"] = $aId;
}
echo json_encode($response);
?>
