module ScoreboardSpec (spec) where

import           Scoreboard
import           Test.Hspec
import           Test.QuickCheck

spec :: Spec
spec = do
  describe "Scoreboard" $ do
    it "current score of newScoreboard is 0 : 0" $ do
      currentScore newScoreboard `shouldBe` (0, 0)
    it "in newScoreboard no team is selected" $ do
      currentSelection newScoreboard `shouldBe` None
    it "selecting a team" $ do
      let scoreboardA = selectTeam newScoreboard TeamA
      currentSelection scoreboardA `shouldBe` TeamA
      let scoreboardB = selectTeam scoreboardA TeamB
      currentSelection scoreboardB `shouldBe` TeamB
    it "incrementing score" $ do
      let scoreboardA = (Scoreboard (1, 2) TeamA)
      incrementScore scoreboardA `shouldBe` (Scoreboard (2, 2) TeamA)
      let scoreboardB = (Scoreboard (1, 2) TeamB)
      incrementScore scoreboardB `shouldBe` (Scoreboard (1, 3) TeamB)
    it "decrementing score" $ do
      let scoreboardA = (Scoreboard (10, 10) TeamA)
      decrementScore scoreboardA `shouldBe` (Scoreboard (9, 10) TeamA)
      let scoreboardB = (Scoreboard (10, 10) TeamB)
      decrementScore scoreboardB `shouldBe` (Scoreboard (10, 9) TeamB)
    it "dont incrementing or decrement with no team selected" $ do
      let scoreboard = (Scoreboard (1, 2) None)
      incrementScore scoreboard `shouldBe` (Scoreboard (1, 2) None)
      decrementScore scoreboard `shouldBe` (Scoreboard (1, 2) None)

  describe "Scoreboard Properties" $ do
    it "decrementing is always possible" $ property $
      prop_decrementing

prop_decrementing :: Scoreboard -> Bool
prop_decrementing scoreboard =
    scoreA >= 0 && scoreB >= 0 where
        decrementedBoard = decrementScore scoreboard
        (scoreA, scoreB) = currentScore decrementedBoard

instance Arbitrary Scoreboard where
   arbitrary = do
     a <- suchThat arbitrary (>= 0)
     b <- suchThat arbitrary (>= 0)
     s <- elements [TeamA, TeamB]
     return $ Scoreboard (a, b) s
