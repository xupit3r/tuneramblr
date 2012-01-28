(ns songmap.models.song
  (:require clj-record.boot)
  (:use [songmap.models.dbdef]))

;; prepare the typical
;; db methods
(clj-record.core/init-model
  :table-name "song")

