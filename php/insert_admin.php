<?php
error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon_admin.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전>달 받습니다.

        $aId=$_POST['aId'];
        $aPwd=$_POST['aPwd'];
        $aPlot=$_POST['aPlot'];

        if(empty($aId)){
            $errMSG = "차량번호를 입력하세요.";
        }
        else if(empty($aPwd)){
            $errMSG = "소유자명을 입력하세요.";
        }
       else if(empty($aPlot)){
            $errMSG = "소유자 연락처를 입력하세요.";
        }

        if(!isset($errMSG)) // 모두 입력이 되었다면
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 admin 테이블에 저장합>니다.
                $stmt = $con->prepare('INSERT INTO admin(aId, aPwd, aPlot) VALUES(:aId, :aPwd, :aPlot)');
                $stmt->bindParam(':aId', $aId);
           $stmt->bindParam(':aPwd', $aPwd);
                $stmt->bindParam(':aPlot', $aPlot);

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
                관리자 아이디: <input type = "text" name = "aId" />
          관리자 비밀번호: <input type = "text" name = "aPwd" />
          관리자 근무장소 : <input type = "text" name = "aPlot" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>
