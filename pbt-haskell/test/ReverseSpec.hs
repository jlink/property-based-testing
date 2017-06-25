module ReverseSpec (spec) where

import           Test.Hspec
import           Test.QuickCheck

spec :: Spec
spec = do
  describe "Reverse Properties" $ do
    it "reversing the reversed" $ property $
      prop_reversed

prop_reversed :: [Int] -> Bool
prop_reversed xs =
    reverse (reverse xs) == xs
