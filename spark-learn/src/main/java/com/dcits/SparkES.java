package com.dcits;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.spark_project.guava.collect.ImmutableList;
import org.spark_project.guava.collect.ImmutableMap;

/**
   * <h1>Use Spark to get and write elasticsearch data.</h1>
   * <p>
   * <b>Note:</b>This class use spark and spark streaming method, 
   * which will be split to two class later.
   * @author kongxw@dcits.com
   * @version 1.0.0
   * @since 2019-10-19
   */
public class SparkES {

    private  Map<String, String> sparkProperties;
    private SparkConf conf;
    private JavaSparkContext sc;
    private String sparkMaster;
    private String appName;
    private String esHost;
    public void init()
    {

        sparkMaster = "local[*]";
        appName = "SparkFileSumApp";
        esHost = "10.7.19.116:30011";
        sparkProperties = new LinkedHashMap<>();
        conf = new SparkConf().setMaster(sparkMaster).setAppName(appName);
        

        sparkProperties.put("spark.ui.enabled", "false");
        // don't die if there are no spans
        sparkProperties.put("es.index.read.missing.as.empty", "true");
        // sparkProperties.put("es.nodes.wan.only", "false");
        sparkProperties.put("es.net.ssl.keystore.location", "");
        sparkProperties.put("es.net.ssl.keystore.pass", "");
        sparkProperties.put("es.net.ssl.truststore.location", "");
        sparkProperties.put("es.net.ssl.truststore.pass", "");

        conf.set("es.nodes", esHost);
        conf.set("es.nodes.discovery", "false");
        conf.set("es.nodes.client.only", "false");
        conf.set("es.nodes.wan.only", "true");
        sc = new JavaSparkContext(conf);

        
    }
        

        
        /**
         * This method is used to test spark. This is
         * a the simplest form of a class method, just to
         * show the usage spark within elasticsearch.
         */
        public void sparkTest(){

         // JavaRDD<Map<String, Object>> links =
            // JavaEsSpark.esJsonRDD(sc, "zipkin:span-2019-10-16/span")
            // .groupBy(JSON_TRACE_ID)
            // .flatMapValues(new TraceIdAndJsonToDependencyLinks(logInitializer,
            // SpanBytesDecoder.JSON_V2))
            // .values()
            // .mapToPair(l -> Tuple2.apply(Tuple2.apply(l.parent(), l.child()), l))
            // .reduceByKey((l, r) -> DependencyLink.newBuilder()
            // .parent(l.parent())
            // .child(l.child())
            // .callCount(l.callCount() + r.callCount())
            // .errorCount(l.errorCount() + r.errorCount())
            // .build())
            // .values()
            // .map(DEPENDENCY_LINK_JSON);
          JavaRDD<String> input = sc.textFile("spark-learn/data.txt"); 
          JavaRDD<String> numberStrings = input.flatMap(s -> Arrays.asList(s.split(" ")).iterator());
          JavaRDD<String> validNumberString = numberStrings.filter(string -> !string.isEmpty()); 
          JavaRDD<Integer> numbers = validNumberString.map(numberString -> Integer.valueOf(numberString)); int
          finalSum = numbers.reduce((x,y) -> x+y);
          
          System.out.println("Final sum is: " + finalSum);
         
        }

        /**
         * This method is used to query elasticsearch data.
         */
        public void queryES(){

        try {
           
            JavaPairRDD<String, Map<String, Object>> esRDD = JavaEsSpark.esRDD(sc, "spark/docs");
            esRDD.foreach(m -> {
                System.out.println(m);
                System.out.println(m._1());
                System.out.println(m._2());
            });

            JavaRDD<Map<String, Object>> esRDD2 = JavaEsSpark.esRDD(sc, "spark/docs").values();
           
            
            esRDD2.foreach(m->{
                m.forEach((k,v)->{
                    System.out.println(k+"----");
                    System.out.println(v);
                });
                System.out.println(m);
            });

            JavaPairRDD<String,String> ses = JavaEsSpark.esJsonRDD(sc, "spark/docs");
            ses.foreach(m -> {
                System.out.println(m);
                System.out.println(m._1());
                System.out.println(m._2());
            });
             
          } finally {
            sc.stop();
          }
        }

        /**
         * This method is used to save elasticsearch data by spark.
         */
        public void saveToES(){

            try {
                
    
                Map<String, ?> numbers = ImmutableMap.of("one", 1, "two", 2);
                Map<String, ?> airports = ImmutableMap.of("OTP", "Otopeni", "SFO", "San Fran");
    
                JavaRDD<Map<String, ?>> javaRDD = sc.parallelize(ImmutableList.of(numbers,airports));
                JavaEsSpark.saveToEs(javaRDD, "spark/docs");
    
                
              } finally {
                sc.stop();
              }
            }

}