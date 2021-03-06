(ns samlib.parse
  (:require [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]))


(System/setProperty "http.proxyHost" "localhost")
(System/setProperty "http.proxyPort" "8888")


;; (def *result* (http/get *samlib-link* {:as :auto})) ;; "windows-1251"

(comment
  (System/getProperty "user.dir") "d:\\clojtemp\\samlib"
  (spit "test.html" (*result* :body))
  (slurp "test.html")
  )


(def *samlib-link* "http://samlib.ru/s/subbota_i/")
(def *samlib-link-ends* "indexdate.shtml")
(def *result* (fetch-link (str *samlib-link* *samlib-link-ends*))) 
(def *test-res* (fetch-link "http://samlib.ru/m/minaew_d_n/wilja_5.shtml"))


(defn fetch-link
  [link]
  (let [link-to-fetch link]
    (http/get link-to-fetch {:as :auto})))

(defn extractor [ begpat endpat]
  (fn [page-result]
    (-> page-result
        :body
        (clojure.string/split begpat)
        last
        (clojure.string/split endpat)
        first
        )))

(def main-page-ext
  (extractor #"<!-------- ������ <body> ��������� ������ �� ������������! ------>"
             #"<!--------- �������� ------------------------------->"))

(def page-text-ext
  (extractor #"<!--Section Begins-->"
             #"<!--Section Ends-->"))

(def page-anno-ext
  (extractor #"<!---- ���� �������� ������������ \(����� ������\) ----------------------->"
             #"<!---------- ������ ������ ����������������� -------->"))

(defn text-hash [text]
  (count text))

(defn main-page-worker
  [fetch-result]
  (let [comments-to-remove #"/comment/.*"
        res (-> fetch-result
                main-page-ext
                java.io.StringReader.
                html/html-resource
                (html/select [:a]))
        dirty-links (map #(->> % :attrs :href) res)
        links (remove #(re-matches comments-to-remove %) dirty-links)
        ]
    links))

(defn page-worker
  [fetch-result]
  (let [anno (-> fetch-result
                 page-anno-ext
                 )
        link (first (fetch-result :trace-redirects))
        date-created ((first (re-seq #"��������: (\d+/\d+/\d+)" anno)) 1)
        date-modified ((first (re-seq #", �������: (\d+/\d+/\d+)" anno)) 1)
        size ((first (re-seq #"\. (\d+)k\." anno)) 1)
        fb2 ((first (re-seq #"href=\"(.+\.fb2\.zip)" anno)) 1)
        title ((first (re-seq #"<title>(.*)</title>" (:body fetch-result))) 1)
        texthash (-> fetch-result
                      page-text-ext
                      count
                      )
        annot ((first (re-seq #"���������:.*<i>(.*)</i." (:body fetch-result))) 1)
           ]
    [date-created, date-modified, size, link, title, texthash, fb2, annot
     ]))


(defn crawl [link-list]
  (let [crawl-list (map #(str % *samlib-link-ends*) link-list)
        ]))

;; (defn links-ext
;;   [main-page-stories-ext-res]
;;   (let [comments-to-remove #"/comment/.*"
;;         res (-> main-page-stories-ext-res
;;                 java.io.StringReader.
;;                 html/html-resource
;;                 )
;;         pre-links  (->> (html/select res [:a])
;;                         (partition 1 2)
;;                         flatten
;;                         )
;;         bold (html/select res [:b])

;;         links (->> (map #(-> %                   
;;                              :attrs
;;                              :href) pre-links)
;;                    )
;;         size (->> (map :content bold )
;;                   (partition 2 3)
;;                   (map last)
;;                   flatten
;;                   )
;;         text (map html/text pre-links)        
;;         ]

;;     (->> (interleave links text size)
;;          (partition 3)
;;          )
;;     ))





(links-ext (stories-ext *result*))

(take-nth 2 (links-ext (stories-ext *result*)))










