(defproject configleaf "0.3.0"
  :description "Software configuration for Clojure"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [stencil "0.1.2"]
                 [robert/hooke "1.1.2"]]
  :dev-dependencies [[ritz "0.1.7"]]

  :eval-in-leiningen true
  :hooks [configleaf.hooks]

  :configleaf {:profiles {:test {:params {:test 1}
                                 :java-properties {"test" "1"
                                                   :test2 "2"}}}
               :namespace cfg.test-ns})