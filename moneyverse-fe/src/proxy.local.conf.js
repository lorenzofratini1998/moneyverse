const PROXY_CONFIG = {
  "/currencies": {
    "target": "http://localhost:7001",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        console.log('Skipping proxy for browser request.');
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/currencies": "/currencyManagement/api/v1/currencies"
    }
  },
  "/users/**": {
    "target": "http://localhost:7002",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/users": "/usersManagement/api/v1/users"
    }
  },
  "/preferences*": {
    "target": "http://localhost:7002",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/preferences": "/usersManagement/api/v1/preferences"
    }
  },
  "/languages/**": {
    "target": "http://localhost:7002",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/languages": "/usersManagement/api/v1/languages"
    }
  },
  "/accounts/sse": {
    "target": "http://localhost:7003",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/accounts/sse": "/accountsManagement/api/v1/sse"
    }
  },
  "/accounts/**": {
    "target": "http://localhost:7003",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/accounts": "/accountsManagement/api/v1/accounts"
    }
  },
  "/budgets/sse": {
    "target": "http://localhost:7004",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/budgets/sse": "/budgetsManagement/api/v1/sse"
    }
  },
  "/budgets/**": {
    "target": "http://localhost:7004",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/budgets": "/budgetsManagement/api/v1/budgets"
    }
  },
  "/categories": {
    "target": "http://localhost:7004",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/categories": "/budgetsManagement/api/v1/categories"
    }
  },
  "/categories/**": {
    "target": "http://localhost:7004",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/categories": "/budgetsManagement/api/v1/categories"
    }
  },
  "/transactions/sse": {
    "target": "http://localhost:7005",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/transactions/sse": "/transactionsManagement/api/v1/sse"
    }
  },
  "/tags/**": {
    "target": "http://localhost:7005",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/tags": "/transactionsManagement/api/v1/tags"
    }
  },
  "/transfers/**": {
    "target": "http://localhost:7005",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/transfers": "/transactionsManagement/api/v1/transfers"
    }
  },
  "/subscriptions/**": {
    "target": "http://localhost:7005",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/subscriptions": "/transactionsManagement/api/v1/subscriptions"
    }
  },
  "/transactions/**": {
    "target": "http://localhost:7005",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/transactions(?!/sse)": "/transactionsManagement/api/v1/transactions"
    }
  },
  "/analytics/sse": {
    "target": "http://localhost:7006",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/analytics/sse": "/analytics/api/v1/sse"
    }
  },
  "/analytics/**": {
    "target": "http://localhost:7006",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "bypass": function (req, res, proxyOptions) {
      if (req.headers.accept && req.headers.accept.includes('text/html')) {
        return '/index.html';
      }
    },
    "pathRewrite": {
      "^/analytics": "/analytics/api/v1"
    }
  }
};

module.exports = PROXY_CONFIG;
