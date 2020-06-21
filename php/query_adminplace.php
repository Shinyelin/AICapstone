<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon_admin.php');



//POST 값을 읽어온다.
$aId=isset($_POST['aId']) ? $_POST['aId'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");




if ($aId != "" ){

    $sql="select aPlot  from admin where aId='$aId'";
    $stmt = $con->prepare($sql);
    $stmt->execute();

    if ($stmt->rowCount() == 0){
        echo "'";
        echo $aId;
        echo "'은 찾을 수 없습니다.";
    }
        else{

                $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

                extract($row);
        function han ($s) { return reset(json_decode('{"s":"'.$s.'"}')); }
        function to_han ($str) { return preg_replace('/(\\\u[a-f0-9]+)+/e','han("$0")',$str); }

       $json=to_han(json_encode($row["aPlot"]));
        echo $row["aPlot"];


        }

    }

}
else {
    echo "아이디를 입력하세요";
}

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         아이디: <input type = "text" name = "aId" />

         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>

