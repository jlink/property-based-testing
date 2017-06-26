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
prop_divisible_by_3 = forAll divisibleBy3 $ \i ->
    isInfixOf "Fizz" (fizzBuzz i)

divisibleBy3 :: Gen Int
divisibleBy3 = (\i -> i * 3) `fmap` choose (1, 33)

prop_undivisibles :: Property
prop_undivisibles = forAll (choose(1, 100)) $ \i ->
    (i `mod` 3 /= 0) && (i `mod` 5 /= 0) ==> (fizzBuzz i) == (show i)

prop_divisible_by_5 :: Property
prop_divisible_by_5 = forAll (choose(1, 100)) $ \i ->
    (i `mod` 5 == 0) ==> isInfixOf "Buzz" (fizzBuzz i)

fizzBuzz :: Int -> String
fizzBuzz aNumber = if (aNumber `mod` 3 == 0)
  then "Fizz"
  else show aNumber
