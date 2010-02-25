(use 'clj-unit.core)
(use 'facebook)

(def method-tests
  ['facebook.fql-test])

(apply require-and-run-tests method-tests)