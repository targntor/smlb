(defproject samlib "0.1.0-SNAPSHOT"
  :description "MyFirstSamlibParser"
  :url "http://"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "0.9.2"]
                 [enlive "1.1.5"]]
  :main ^:skip-aot samlib.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
