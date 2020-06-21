<?php
error_reporting(E_ALL);
ini_set('display_errors',1);

include('dbcon_admin.php');



//POST 값을 읽어온다.
$aId=isset($_POST['aId']) ? $_POST['aId'] : '';
$aPwd = isset($_POST['aPwd']) ? $_POST['aPwd'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


if ($aId != "" ){

    $sql="select * from admin where aId='$aId' and aPwd='$aPwd'";
    $stmt = $con->prepare($sql);
    $stmt->execute();

    if ($stmt->rowCount() == 0){
        echo "'";
        echo $aId,", ",$aPwd;
        echo "'은 찾을 수 없습니다.";
    }
        else{

                $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC)){

                extract($row);

            array_push($data,
                array('id'=>$row["id"],
                'aId'=>$row["aId"],
                'aPwd'=>$row["aPwd"],
                                'aPlot'=>$row["aPlot"]
            ));
        }


        if (!$android) {
            echo "<pre>";
            print_r($data);
            echo '</pre>';
        }else
        {
            echo "환영합니다";
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
         비밀번호: <input type = "text" name = "aPwd" />

         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
