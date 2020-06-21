<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon_admin.php');



//POST 값을 읽어온다.
$pName=isset($_POST['pName']) ? $_POST['pName'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($pName != "" ){

    $sql="select * from parkinglot where pName='$pName'";
    $stmt = $con->prepare($sql);
    $stmt->execute();

    if ($stmt->rowCount() == 0){

        echo "'";
        echo $pName;
        echo "'은 찾을 수 없습니다.";
    }
        else{

                $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

                extract($row);

            echo $row["pAdd"];

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
         장소: <input type = "text" name = "pName" />

         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>

