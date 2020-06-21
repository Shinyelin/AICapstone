<?php
error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon_admin.php');

    $cnum=isset($_POST['cnum']) ? $_POST['cnum'] : '';

    $stmt = $con->prepare("select * from car where cnum ='$cnum'");
    $stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);

            array_push($data,
                array(
                 'cowner'=>$cowner,
                                'cpenalty'=>$cpenalty

            ));
        }

        header('Content-Type: application/json; charset=utf8');
                function han ($s) { return reset(json_decode('{"s":"'.$s.'"}')); }
        function to_han ($str) { return preg_replace('/(\\\u[a-f0-9]+)+/e','han("$0")',$str); }
        $json = to_han(json_encode(array("webnautes"=>$data)));

        echo $json;
    }

?>
<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>
<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         차량번호: <input type = "text" name = "cnum" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>

