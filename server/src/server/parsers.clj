(ns server.parsers)

;;TODO remove string &quot;
;;  &#160;
;;  &nbsp;

(defn remove-html-tags [html]
  (.replaceAll html "<[^>]*>" ""))
