(ns tic-tac-toe.core
  (:require
    [reagent.core :as r]
    [clojure.set :as set]))

(def possible-comb #{#{0 1 2} #{3 4 5} #{6 7 8}
                     #{0 4 8} #{2 4 6} #{0 3 6}
                     #{1 4 7} #{2 5 8}})

(defonce board (r/atom (vec (repeat 9 nil))))

(defonce players (r/atom [{:sym "X" :moves #{}}
                          {:sym "O" :moves #{}}]))

(defn has-won []
  (let [current-player-move (:moves (first @players))]
    (if-not (empty? current-player-move)
      (some #(set/subset? % current-player-move) possible-comb)
      false)))

(defn update-player [player pos]
  (update-in player [:moves] #(conj % pos)))

(defn update-players [players, pos]
  (->> (update players 0 #(update-player % pos))
       reverse
       vec))

(defn update-game [sym pos]
  (do (swap! board update-in [pos] str sym)
      (swap! players update-players pos)))

(defn make-move [sym pos]
  (if (and (nil? (get @board pos)) (not (has-won)))
    (update-game sym pos)
    [:div "Invalid Move"]))

(defn generate-cell [pos sym]
  (println pos, sym)
  [:div {:class    "cell"
         :on-click #(make-move (:sym (first @players)) pos)}
   sym])

(defn draw-board []
  [:div {:class "board"} (map-indexed generate-cell @board)])

(defn mount-root []
  (r/render [draw-board] (.getElementById js/document "app")))

(defn init! []
  (mount-root))