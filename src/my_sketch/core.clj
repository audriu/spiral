(ns my-sketch.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (let [max-r (/ (q/width) 2)
        n (int (q/map-range (q/width)
                            200 500
                            20 50))]

    {:dots (into [] (for [r (map #(* max-r %)
                                 (range 0 1 (/ n)))]
                      [(int r) 0]))}))

(def speed 0.0005)

(defn move [dot]
  (let [[r a] dot]
    [r (+ a (* r speed))]))

(defn update-state [state]
  (update-in state [:dots] #(map move %)))

(defn dot->coord [[r a]]
  [(+ (/ (q/width) 2) (* r (q/cos a)))
   (+ (/ (q/height) 2) (* r (q/sin a)))])

(defn draw-state [state]
  (q/background 248 177 150)
  (q/fill 0)
  (let [dots (:dots state)]
    (loop [curr (first dots)
           tail (rest dots)
           prev nil]
      (let [[x y] (dot->coord curr)]
        (q/ellipse x y 5 5)
        (when prev
          (let [[x2 y2] (dot->coord prev)]
            (q/line x y x2 y2))))
      (when (seq tail)
        (recur (first tail)
               (rest tail)
               curr)))))

(q/defsketch my-sketch
  :host "host"
  :size [500 700]
  :setup setup
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])