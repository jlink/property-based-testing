module FizzBuzzSpec (spec) where

import           Data.List
import           Test.Hspec
import           Test.QuickCheck

spec :: Spec
spec = do
  describe "FizzBuzz" $ do
    it "divisible by 3 contains Fizz" $ property $
      prop_divisible_by_3
    it "undivisibles return themsselves" $ property $
      prop_undivisibles
    it "divisible by 5 contains Buzz" $ property $
      prop_divisible_by_5

prop_divisible_by_3 :: Property
prop_divisible_by_3 = forAll genNumbers $ \i ->
    (i `mod` 3 == 0) ==> isInfixOf "Fizz" (fizzBuzz i)

prop_undivisibles :: Property
prop_undivisibles = forAll genNumbers $ \i ->
    (i `mod` 3 /= 0) && (i `mod` 5 /= 0) ==> (fizzBuzz i) == (show i)

prop_divisible_by_5 :: Property
prop_divisible_by_5 = forAll genNumbers $ \i ->
    (i `mod` 5 == 0) ==> isInfixOf "Buzz" (fizzBuzz i)

genNumbers :: Gen Int
genNumbers = abs `fmap` (arbitrary :: Gen Int) `suchThat` \x -> (x > 0) && (x <= 100)

fizzBuzz :: Int -> String
fizzBuzz aNumber = if (aNumber `mod` 3 == 0)
  then "Fizz"
  else show aNumber
