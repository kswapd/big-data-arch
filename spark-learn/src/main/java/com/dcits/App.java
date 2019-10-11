package com.dcits;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        /*SparkConf conf;
        String sparkMaster = "local[*]";
        conf = new SparkConf(true).setMaster(sparkMaster).setAppName("test");
        JavaSparkContext ctx = new JavaSparkContext(conf);*/

        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("SparkFileSumApp");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> input = sc.textFile("spark-learn/data.txt");
        JavaRDD<String> numberStrings = input.flatMap(s -> Arrays.asList(s.split(" ")).iterator());
        JavaRDD<String> validNumberString = numberStrings.filter(string -> !string.isEmpty());
        JavaRDD<Integer> numbers = validNumberString.map(numberString -> Integer.valueOf(numberString));
        int finalSum = numbers.reduce((x,y) -> x+y);

        System.out.println("Final sum is: " + finalSum);

        sc.close();
        System.out.println( "Hello World!" );
    }
}

