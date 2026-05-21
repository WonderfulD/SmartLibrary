import request from '@/utils/request'

// 订购图书
export function orderBook(data) {
  return request({
    url: '/bookorder/BookOrder',
    method: 'post',
    data: data
  })
}
