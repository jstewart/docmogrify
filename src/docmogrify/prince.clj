(ns docmogrify.prince
  (:require [clojure.java.io    :as io]
            [clojure.string     :as s]
            [clojure.java.shell :refer [sh]]))

(def allowed-args [:--baseurl :--http-user :--http-password
                   :--http-proxy :--http-timeout :--media
                   :--owner-password :--user-password
                   :--input :--key-bits])

(def allowed-flags [:--no-xinclude :--no-network :--no-author-style
                    :--no-default-style :--no-embed-fonts :--no-subset-fonts
                    :--no-compress :--encrypt :--disallow-print :--disallow-copy
                    :--disallow-annotate :--disallow-modify :--javascript])

(defn normalize-options
  "Normalizes map of prince options (opts) and returns a string of
   command line option args"
  [opts]
  (let [flags (filter #(true? (last %)) (select-keys opts allowed-flags))
        args  (select-keys opts allowed-args)]
    (concat
     (map #(name (first %)) flags)
     (map #(str (name (first %)) " " (last %)) args))))

(defn normalize-input
  "Normalizes different types of possible input for prince.
   If it's a url, deal with that as is. If it's a string, that needs to
   be written to a tmp file and have the name returned"
  [x]
  (if (re-matches #"^http(s?)://.*$" x) x
      (let [tmp (java.io.File/createTempFile "prince-input" ".html")]
        (with-open [file (io/writer tmp)] (.write file x))
        tmp)))

(defn run-cmd
  "creates a command line to be run from a prince profile map (m)
   returning a list of [true <file>] if successful,
   or [false \"error\"] if not.
  "
  [m]
  (let [in  (normalize-input (get m :data))
        out (java.io.File/createTempFile "prince-out" ".pdf")
        cmd (apply
             sh (concat
                 ["prince"]
                 (normalize-options (get m :options {}))
                 [(str  in)]
                 [(str  "-o " out)]))]

    (if (= java.io.File (type in)) (.delete in))
    (if (= 0 (:exit cmd))
      [true out]
      [false (:err cmd)])))
