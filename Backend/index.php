<?php
header("Access-Control-Allow-Origin: *");

// Autocomplete
if(isset($_GET["inputVal"])){
    $result  = file_get_contents('http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input='.$_GET["inputVal"]);
    echo($result);
}

// Price Chart
if(isset($_GET["priceSymbol"])){
    $priceResult = file_get_contents('https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=' . $_GET["priceSymbol"] . '&apikey=JXIE9QZNR7RYE9KA&outputsize=full');
    echo($priceResult);
}

// SMA Chart
if(isset($_GET["smaSymbol"])){
    $smaResult = file_get_contents('https://www.alphavantage.co/query?function=SMA&symbol=' . $_GET["smaSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&nbdevup=3&nbdevdn=3');
    echo($smaResult);
}

// EMA Chart
if(isset($_GET["emaSymbol"])){
    $emaResult = file_get_contents('https://www.alphavantage.co/query?function=EMA&symbol=' . $_GET["emaSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&nbdevup=3&nbdevdn=3');
    echo($emaResult);
}

// RSI Chart
if(isset($_GET["rsiSymbol"])){
    $rsiResult = file_get_contents('https://www.alphavantage.co/query?function=RSI&symbol=' . $_GET["rsiSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&nbdevup=3&nbdevdn=3');
    echo($rsiResult);
}

// ADX Chart
if(isset($_GET["adxSymbol"])){
    $adxResult = file_get_contents('https://www.alphavantage.co/query?function=ADX&symbol=' . $_GET["adxSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&nbdevup=3&nbdevdn=3');
    echo($adxResult);
}

// CCI Chart
if(isset($_GET["cciSymbol"])){
    $cciResult = file_get_contents('https://www.alphavantage.co/query?function=CCI&symbol=' . $_GET["cciSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&nbdevup=3&nbdevdn=3');
    echo($cciResult);
}

// STOCH Chart
if(isset($_GET["stochSymbol"])){
    $stochResult = file_get_contents('https://www.alphavantage.co/query?function=STOCH&symbol=' . $_GET["stochSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&slowkmatype=1&slowdmatype=1');
    echo($stochResult);
}

// BBANDS Chart
if(isset($_GET["bbandsSymbol"])){
    $bbandsResult = file_get_contents('https://www.alphavantage.co/query?function=BBANDS&symbol=' . $_GET["bbandsSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&nbdevup=3&nbdevdn=3');
    echo($bbandsResult);
}

// MACD Chart
if(isset($_GET["macdSymbol"])){
    $macdResult = file_get_contents('https://www.alphavantage.co/query?function=MACD&symbol=' . $_GET["macdSymbol"] . '&interval=daily&time_period=10&series_type=close&apikey=JXIE9QZNR7RYE9KA&nbdevup=3&nbdevdn=3');
    echo($macdResult);
}

// Historical Chart
if(isset($_GET["hisChartSymbol"])){
    $hisChartResult = file_get_contents('https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=' . $_GET["hisChartSymbol"] . '&apikey=JXIE9QZNR7RYE9KA&outputsize=full');
    echo($hisChartResult);
}

// Current Stocks Table
if(isset($_GET["symbolVal"])) {
    $allResult = file_get_contents('https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=' . $_GET["symbolVal"] . '&apikey=JXIE9QZNR7RYE9KA');
    
    $jsonContent = json_decode($allResult);
    
    $metadataKey="Meta Data";
    $timeSeriesKey="Time Series (Daily)";
    $symbolKey="2. Symbol";
    $timeKey="3. Last Refreshed";
    $openKey="1. open";
    $closeKey="4. close";
    $volKey="5. volume";
    $lowKey="3. low";
    $highKey="2. high";
    
    $symbol="";
    $timestamp="";
    $open="";
    $close="";
    $vol="";
    $prevClose="";
    $low="";
    $high="";
    
    if($jsonContent->$metadataKey != null)
    {
        $symbol=$jsonContent->$metadataKey->$symbolKey;
        $timestamp=$jsonContent->$metadataKey->$timeKey;
    }
    
    if($jsonContent->$timeSeriesKey != null)
    {
        $count=0;
        foreach($jsonContent->$timeSeriesKey as $key=>$value)
        {
            if($count==0)
            {
                $open=$value->$openKey;
                $close=$value->$closeKey;
                $vol=$value->$volKey;
                $low=$value->$lowKey;
                $high=$value->$highKey;
                $count=$count+1;
            }
            else if($count==1)
            {
                $prevClose=$value->$closeKey;
                $count=$count+1;
            }
            else if($count==2)
            {
                break;
            } 
        }
    }
    
    $arr=array(
        "Symbol" => $symbol,
        "Close" => $close, 
        "Previous Close" => $prevClose,
        "Timestamp" => $timestamp,
        "Open" => $open,
        "Low" => $low,
        "High" => $high,
        "Volume" => $vol  
    );
    
    $finalResult=json_encode($arr);
    echo($finalResult);
}

// News Feed
if(isset($_GET["newsSymbol"])){
    $newsUrl='https://seekingalpha.com/api/sa/combined/' .$_GET["newsSymbol"]. '.xml';
    $xml=simplexml_load_file($newsUrl) or die("Error: Cannot create object");
    
    $channelKey="channel";
    $itemKey="item";
    $linkKey="link";
    $titleKey="title";
    $pubKey="pubDate";
    $authorKey="author_name";
    $items=$xml->$channelKey->$itemKey;
    $count=1;
    
    $allNewsArr=array();
    for($i=0; $i<(sizeof($items)); $i++)
    {
        if($count===6)
        {
            break;
        }
        
        $itemLink=$items[$i]->$linkKey;
        if(strpos($itemLink, "/article/") !==false)
        {
            $count=$count+1;
            $itemTitle=$items[$i]->$titleKey;
            $itemAuthor=$items[$i]->children('https://seekingalpha.com/api/1.0')->$authorKey;
            $itemPub=$items[$i]->$pubKey;
            
            $arr=array("Title"=>$itemTitle, "Link"=>$itemLink, "Author"=>$itemAuthor, "Date"=>$itemPub);
            array_push($allNewsArr, $arr);
        }
        else
        {
            continue;
        }
    }
    
    $allNewsJson=json_encode($allNewsArr);
    echo($allNewsJson);
}

?>