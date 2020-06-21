<?php
error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon_admin.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 >전달 받습니다.

        $cnum=$_POST['cnum'];


        if(empty($cnum)){
            $errMSG = "차량번호를 입력하세요";
        }


        if(!isset($errMSG)) // 모두 입력이 되었다면
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 admin 테이블에 저장>합니다.
                                $stmt = $con->prepare("update car set cpenalty=cpenalty+10000 where cnum ='$cnum'");

                if($stmt->execute())
                {
                    $successMSG = "새로운 사용자를 추가했습니다.";
                }
                else
                {
                    $errMSG = "벌금 추가 에러";
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
                cnum: <input type = "text" name = "cnum" />

                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>

