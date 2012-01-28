(ns songmap.models.metadata  
  (:import (java.util.Set)
           (org.apache.hadoop.hbase HBaseConfiguration)
           (org.apache.hadoop.hbase.client Put Get HTable)
           (org.apache.hadoop.hbase.util Bytes)))

;; get a handle on an hbase table
(defn hbase-table [tabname]
  (HTable. (HBaseConfiguration.) tabname))