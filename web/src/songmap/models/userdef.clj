(ns songmap.models.metadata  
  (:require clj-record.boot)
  (:use [songmap.models.dbdef]))

;; prepare the typical
;; db methods
(clj-record/init-model)