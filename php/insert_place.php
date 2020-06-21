<?php
    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon_admin.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전>달 받습니다.

        $pName=$_POST['pName'];
        $pAdd=$_POST['pAdd'];
        $pregNo=$_POST['pregNo'];
        $disNo=$_POST['disNo'];
        if(empty($pName)){
            $errMSG = "주차장이름을 입력하세요.";
        }
        else if(empty($pAdd)){
            $errMSG = "주차장 주소를 입력하세요.";
        }
       else if(empty($pregNo)){
            $errMSG = "임산부주차구역 수를 입력하세요.";
        }
       else if(empty($disNo)){
            $errMSG = "장애인주차구역 수를 입력하세요.";
        }
        if(!isset($errMSG)) // 모두 입력이 되었다면
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 car 테이블에 저장합니>다.
                $stmt = $con->prepare('INSERT INTO parkinglot(pName, pAdd, pregNo,disNo) VALUES(:pName, :pAdd, :pregNo,:disNo)');
                $stmt->bindParam(':pName', $pName);
           $stmt->bindParam(':pAdd', $pAdd);
                $stmt->bindParam(':pregNo', $pregNo);
                $stmt->bindParam(':disNo', $disNo);
                if($stmt->execute())
                {
                    $successMSG = "새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage());
            }
        }

    }

?>


<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;
        $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                주차장이름: <input type = "text" name = "pName" />
           주차장주소: <input type = "text" name = "pAdd" />
           임산부주차구역수: <input type = "text" name = "pregNo" />
                장애인주차구역수: <input type = "text" name = "disNo" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>


