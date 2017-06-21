module Scoreboard where

type Score = (Int, Int)
data Selection = TeamA | TeamB | None deriving (Show, Eq, Enum, Bounded)
data Scoreboard = Scoreboard Score Selection deriving (Show, Eq)

newScoreboard :: Scoreboard
newScoreboard = Scoreboard (0, 0) None

currentScore :: Scoreboard -> Score
currentScore (Scoreboard score _) = score

currentSelection :: Scoreboard -> Selection
currentSelection (Scoreboard _ selection) = selection

selectTeam :: Scoreboard -> Selection -> Scoreboard
selectTeam (Scoreboard score _) selection = Scoreboard score selection

incrementScore :: Scoreboard -> Scoreboard
incrementScore (Scoreboard score None) = Scoreboard score None
incrementScore (Scoreboard (a, b) TeamA) = Scoreboard (a + 1, b) TeamA
incrementScore (Scoreboard (a, b) TeamB) = Scoreboard (a, b + 1) TeamB

decrementScore :: Scoreboard -> Scoreboard
decrementScore (Scoreboard score None) = Scoreboard score None
decrementScore (Scoreboard (0, b) TeamA) = Scoreboard (0, b) TeamA
decrementScore (Scoreboard (a, 0) TeamB) = Scoreboard (a, 0) TeamB
decrementScore (Scoreboard (a, b) TeamA) = Scoreboard (a - 1, b) TeamA
decrementScore (Scoreboard (a, b) TeamB) = Scoreboard (a, b - 1) TeamB
